import React, { useState, useEffect } from 'react';
import { 
  Container, Box, Typography, TextField, Button, Paper, Alert, 
  CssBaseline, ThemeProvider, createTheme, AppBar, Toolbar, 
  Select, MenuItem, FormControl, InputLabel, Table, TableBody, 
  TableCell, TableContainer, TableHead, TableRow, Chip, IconButton,
  Tabs, Tab, Grid, Card, CardContent, CardActions, Dialog, DialogTitle, DialogContent, DialogActions
} from '@mui/material';
import LocalHospitalIcon from '@mui/icons-material/LocalHospital';
import LogoutIcon from '@mui/icons-material/Logout';
import RefreshIcon from '@mui/icons-material/Refresh';

const theme = createTheme({
  palette: {
    mode: 'dark',
    primary: { main: '#6366f1' },
    secondary: { main: '#10b981' },
    background: { default: '#0f172a', paper: '#1e293b' }
  },
  typography: { fontFamily: '"Inter", "Roboto", "Helvetica", "Arial", sans-serif' },
  shape: { borderRadius: 12 },
});

export default function App() {
  const [view, setView] = useState('login');
  const [currentUser, setCurrentUser] = useState(() => {
    const saved = window.localStorage.getItem('currentUser');
    return saved ? JSON.parse(saved) : null;
  });
  const [token, setToken] = useState(() => window.localStorage.getItem('token') || '');
  const [userProfileId, setUserProfileId] = useState(() => {
    const saved = window.localStorage.getItem('userProfileId');
    return saved ? JSON.parse(saved) : null;
  });
  
  // Auth State
  const [email, setEmail] = useState('');
  const [password, setPassword] = useState('');
  const [name, setName] = useState('');
  const [role, setRole] = useState('PATIENT');
  const [specialization, setSpecialization] = useState('General');
  
  // Global Data State
  const [users, setUsers] = useState([]);
  const [doctorsList, setDoctorsList] = useState([]);
  const [consultations, setConsultations] = useState([]);
  const [prescriptions, setPrescriptions] = useState([]);
  const [auditLogs, setAuditLogs] = useState([]);
  
  // Form States
  const [symptoms, setSymptoms] = useState('');
  const [selectedDoctorId, setSelectedDoctorId] = useState('');
  
  // Prescription Form UI State
  const [isPrescriptionModalOpen, setPrescriptionModalOpen] = useState(false);
  const [activeConsultation, setActiveConsultation] = useState(null);
  const [diag, setDiag] = useState('');
  const [notes, setNotes] = useState('');
  const [medName, setMedName] = useState('');
  const [dosage, setDosage] = useState('');
  const [freq, setFreq] = useState('');
  const [duration, setDuration] = useState('');
  
  // Prescription Templates
  const [prescriptionTemplates, setPrescriptionTemplates] = useState([]);
  const [selectedTemplate, setSelectedTemplate] = useState('');
  
  const [error, setError] = useState('');
  const [success, setSuccess] = useState('');
  const [loading, setLoading] = useState(false);
  const [tabIndex, setTabIndex] = useState(0);

  const getAuthHeaders = (authToken = token) => authToken ? { Authorization: `Bearer ${authToken}` } : {};

  const authorizedFetch = async (url, options = {}, authToken = token) => {
    return fetch(url, {
      ...options,
      headers: {
        ...(options.headers || {}),
        ...getAuthHeaders(authToken)
      }
    });
  };

  useEffect(() => {
    if (token) {
      localStorage.setItem('token', token);
    } else {
      localStorage.removeItem('token');
    }
    if (currentUser) {
      localStorage.setItem('currentUser', JSON.stringify(currentUser));
    } else {
      localStorage.removeItem('currentUser');
    }
    if (userProfileId) {
      localStorage.setItem('userProfileId', JSON.stringify(userProfileId));
    } else {
      localStorage.removeItem('userProfileId');
    }
  }, [token, currentUser, userProfileId]);

  const fetchGlobalData = async (userRole = currentUser?.role, authToken = token) => {
    try {
      const usersPromise = userRole === 'ADMIN'
        ? authorizedFetch('http://127.0.0.1:8080/api/admin/users', {}, authToken)
        : Promise.resolve({ ok: false });
      const docsPromise = fetch('http://127.0.0.1:8080/api/doctor-profiles');
      const consPromise = authToken
        ? authorizedFetch(userRole === 'ADMIN' ? 'http://127.0.0.1:8080/api/admin/consultations' : 'http://127.0.0.1:8080/api/consultations', {}, authToken)
        : Promise.resolve({ ok: false });
      const presPromise = authToken
        ? authorizedFetch('http://127.0.0.1:8080/api/prescriptions', {}, authToken)
        : Promise.resolve({ ok: false });
      const tempPromise = fetch('http://127.0.0.1:8080/api/prescription-templates');

      const [usersRes, docsRes, consRes, presRes, tempRes] = await Promise.all([
        usersPromise,
        docsPromise,
        consPromise,
        presPromise,
        tempPromise
      ]);

      if (usersRes.ok) setUsers(await usersRes.json());
      if (docsRes.ok) setDoctorsList(await docsRes.json());
      if (consRes.ok) setConsultations(await consRes.json());
      if (presRes.ok) setPrescriptions(await presRes.json());
      if (tempRes.ok) setPrescriptionTemplates(await tempRes.json());
    } catch (e) {
      console.error("Failed fetching data:", e);
    }
  };

  const handleLogin = async (e) => {
    e.preventDefault();
    setLoading(true); setError(''); setSuccess('');
    try {
      const res = await fetch('http://127.0.0.1:8080/api/auth/login', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ email, password })
      });

      if (!res.ok) {
        const message = await res.text();
        setError(message || 'Invalid email or password.');
        setLoading(false);
        return;
      }

      const authData = await res.json();
      setToken(authData.token);
      const user = { name: authData.name, email: authData.email, role: authData.role };
      setCurrentUser(user);

      const profileUrl = authData.role === 'PATIENT'
        ? 'http://127.0.0.1:8080/api/patient-profiles/me'
        : authData.role === 'DOCTOR'
          ? 'http://127.0.0.1:8080/api/doctor-profiles/me'
          : null;

      if (profileUrl) {
        const profileRes = await authorizedFetch(profileUrl, {}, authData.token);
        if (profileRes.ok) {
          const profileData = await profileRes.json();
          if (profileData?.id) {
            setUserProfileId(profileData.id);
          } else {
            setError('Failed to resolve your profile after login.');
          }
        } else {
          const message = await profileRes.text();
          setError(`Failed to load profile: ${message}`);
        }
      }

      setView('dashboard');
      setTabIndex(0);
      fetchGlobalData(authData.role, authData.token);
      setEmail(''); setPassword('');
    } catch (err) {
      console.error(err);
      setError('Connection refused. Is Spring Boot running?');
    }
    setLoading(false);
  };

  const handleRegister = async (e) => {
    e.preventDefault();
    setLoading(true); setError(''); setSuccess('');
    try {
      const res = await fetch('http://127.0.0.1:8080/api/auth/register', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({
          name,
          email,
          password,
          role,
          specialization: role === 'DOCTOR' ? specialization : null
        })
      });
      if (res.ok) {
        setSuccess('Registration successful! You can now log in.');
        setName(''); setEmail(''); setPassword(''); setRole('PATIENT');
        setView('login');
      } else {
        const message = await res.text();
        setError(message || 'Failed to register. Email already exists.');
      }
    } catch (err) {
      console.error(err);
      setError('Backend connection failed.');
    }
    setLoading(false);
  };

  const logout = () => {
    setCurrentUser(null);
    setToken('');
    setUserProfileId(null);
    setView('login');
    setTabIndex(0);
    setUsers([]);
    setDoctorsList([]);
    setConsultations([]);
    setPrescriptions([]);
    setPrescriptionTemplates([]);
    setEmail('');
    setPassword('');
    setName('');
    setError('');
    setSuccess('');
    localStorage.removeItem('token');
    localStorage.removeItem('currentUser');
    localStorage.removeItem('userProfileId');
  };

  useEffect(() => {
    if (currentUser && token) {
      setView('dashboard');
      fetchGlobalData(currentUser.role);
    }
  }, []);

  // ----- PATIENT ACTIONS -----
  const handleBookConsultation = async () => {
    if (!selectedDoctorId || !symptoms) return alert("Select doctor and enter symptoms!");
    if (!userProfileId) return alert("Patient profile not loaded. Please refresh or re-login.");
    try {
      const payload = {
        patientId: Number(userProfileId),
        doctorId: Number(selectedDoctorId),
        symptoms: symptoms,
        consultationType: "VIDEO"
      };
      const res = await authorizedFetch('http://127.0.0.1:8080/api/consultations', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(payload)
      });
      if (res.ok) {
        alert("Consultation booked successfully - Status: SCHEDULED!");
        setSymptoms('');
        fetchGlobalData();
      } else {
        const errorText = await res.text();
        alert(`Failed to book consultation. ${errorText}`);
      }
    } catch (err) {
      console.error(err);
    }
  };

  // ----- DOCTOR ACTIONS -----
  const handleStartConsultation = async (consultationId) => {
    try {
      const res = await authorizedFetch(`http://127.0.0.1:8080/api/consultations/${consultationId}/start?doctorId=${userProfileId}`, { method: 'PUT' });
      if(res.ok) {
         alert("Consultation status changed to IN_PROGRESS!");
         fetchGlobalData();
      } else {
         alert("Failed to start consultation. Check status.");
      }
    } catch(err) {
      console.error(err);
    }
  };

  const initiateCompleteConsultation = (c) => {
    setActiveConsultation(c);
    setPrescriptionModalOpen(true);
  };

  const submitPrescriptionAndComplete = async () => {
    if(!activeConsultation) return;
    try {
      // 1. Complete the Consultation
      const cRes = await authorizedFetch(`http://127.0.0.1:8080/api/consultations/${activeConsultation.id}/complete`, {
        method: 'PUT',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ diagnosis: diag, notes: notes })
      });

      // 2. Issue the Prescription attached to this completed consultation
      if (cRes.ok) {
         const pPayload = {
            digitalSignature: currentUser.name + "_" + new Date().getTime(),
            items: [{
               medicineName: medName,
               dosage: dosage,
               frequency: freq,
               duration: duration,
               instructions: "Take as prescribed"
            }]
         };
         const pRes = await authorizedFetch(`http://127.0.0.1:8080/api/prescriptions?consultationId=${activeConsultation.id}`, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(pPayload)
         });
         
         if(pRes.ok) {
           alert("Consultation Completed & Digital Prescription Issued!");
         } else {
           alert("Consultation marked completed, but prescription failing backend checks.");
         }
      } else {
         alert("Failed to mark consultation complete.");
      }
      
      setPrescriptionModalOpen(false);
      setDiag(''); setNotes(''); setMedName(''); setDosage(''); setFreq(''); setDuration('');
      setSelectedTemplate('');
      fetchGlobalData();
    } catch (err) {
      console.error(err);
    }
  };

  // ----- PHARMACIST ACTIONS -----
  const handleDispensePrescription = async (prescriptionId) => {
    try {
      const res = await authorizedFetch(`http://127.0.0.1:8080/api/prescriptions/${prescriptionId}/dispense`, { method: 'PUT' });
      if(res.ok) {
         alert("Prescription has been verified and DISPENSED!");
         fetchGlobalData();
      } else {
         alert("Failed to dispense prescription. Needs to be ACTIVE and valid.");
      }
    } catch (err) {
      console.error(err);
    }
  };

  const handleTemplateSelect = (templateCondition) => {
    const template = prescriptionTemplates.find(t => t.condition === templateCondition);
    if (template) {
      setDiag(template.diagnosis);
      setNotes(template.notes);
      if (template.medicines && template.medicines.length > 0) {
        const med = template.medicines[0]; // Use first medicine for simplicity
        setMedName(med.medicineName);
        setDosage(med.dosage);
        setFreq(med.frequency);
        setDuration(med.duration);
      }
    }
  };

  if (view === 'login' || view === 'register') {
    return (
      <ThemeProvider theme={theme}>
        <CssBaseline />
        <Container component="main" maxWidth="xs" sx={{ height: '100vh', display: 'flex', alignItems: 'center' }}>
          <Paper elevation={16} sx={{ p: 4, width: '100%', borderTop: '4px solid #6366f1' }}>
            <Box display="flex" flexDirection="column" alignItems="center">
              <LocalHospitalIcon color="primary" sx={{ fontSize: 48, mb: 1 }} />
              <Typography component="h1" variant="h5" fontWeight="bold">NexaHealth EHR</Typography>
              <Typography color="text.secondary" sx={{ mb: 3 }}>
                {view === 'login' ? 'Sign in to access your portal' : 'Create a new account'}
              </Typography>

              {error && <Alert severity="error" sx={{ width: '100%', mb: 2 }}>{error}</Alert>}
              {success && <Alert severity="success" sx={{ width: '100%', mb: 2 }}>{success}</Alert>}

              {view === 'login' ? (
                <Box component="form" onSubmit={handleLogin} sx={{ width: '100%' }}>
                  <TextField fullWidth label="Email Address" variant="outlined" margin="normal" value={email} onChange={e => setEmail(e.target.value)} required />
                  <TextField fullWidth label="Password" type="password" variant="outlined" margin="normal" value={password} onChange={e => setPassword(e.target.value)} required />
                  <Button type="submit" fullWidth variant="contained" size="large" sx={{ mt: 3, mb: 2, fontWeight: 'bold' }} disabled={loading}>
                    {loading ? 'Authenticating...' : 'Sign In'}
                  </Button>
                  <Button fullWidth onClick={() => { setView('register'); setError(''); setSuccess(''); setEmail(''); setPassword(''); }} color="secondary">
                    Don't have an account? Register
                  </Button>
                </Box>
              ) : (
                <Box component="form" onSubmit={handleRegister} sx={{ width: '100%' }}>
                  <TextField fullWidth label="Full Name" variant="outlined" margin="normal" value={name} onChange={e => setName(e.target.value)} required />
                  <TextField fullWidth label="Email Address" type="email" variant="outlined" margin="normal" value={email} onChange={e => setEmail(e.target.value)} required />
                  <TextField fullWidth label="Password" type="password" variant="outlined" margin="normal" value={password} onChange={e => setPassword(e.target.value)} required />
                  <FormControl fullWidth margin="normal">
                    <InputLabel>Role</InputLabel>
                    <Select value={role} label="Role" onChange={e => setRole(e.target.value)}>
                      <MenuItem value="PATIENT">Patient</MenuItem>
                      <MenuItem value="DOCTOR">Doctor</MenuItem>
                      <MenuItem value="PHARMACIST">Pharmacist</MenuItem>
                      <MenuItem value="ADMIN">Admin</MenuItem>
                    </Select>
                  </FormControl>
                  {role === 'DOCTOR' && (
                    <FormControl fullWidth margin="normal">
                      <InputLabel>Specialization</InputLabel>
                      <Select value={specialization} label="Specialization" onChange={e => setSpecialization(e.target.value)}>
                        <MenuItem value="General">General Physician</MenuItem>
                        <MenuItem value="Cardiologist">Cardiologist</MenuItem>
                        <MenuItem value="Neurologist">Neurologist</MenuItem>
                        <MenuItem value="Pediatrician">Pediatrician</MenuItem>
                      </Select>
                    </FormControl>
                  )}
                  <Button type="submit" fullWidth variant="contained" size="large" color="secondary" sx={{ mt: 3, mb: 2, fontWeight: 'bold' }} disabled={loading}>
                    {loading ? 'Processing...' : 'Create Account'}
                  </Button>
                  <Button fullWidth onClick={() => { setView('login'); setError(''); setSuccess(''); setEmail(''); setPassword(''); }} color="primary">
                    Back to Sign In
                  </Button>
                </Box>
              )}
            </Box>
          </Paper>
        </Container>
      </ThemeProvider>
    );
  }

  // --- PORTALS ---

  const renderPatientPortal = () => {
    const myConsultations = consultations.filter(c => c.patient?.id === userProfileId);
    const myPrescriptions = prescriptions.filter(p => p.patient?.id === userProfileId);
    
    return (
      <Grid container spacing={3}>
        <Grid item xs={12} md={6}>
          <Card elevation={4}>
            <CardContent>
              <Typography variant="h6" color="primary" gutterBottom>Book Consultation</Typography>
              <FormControl fullWidth margin="normal">
                <InputLabel>Select Doctor</InputLabel>
                <Select value={selectedDoctorId} label="Select Doctor" onChange={e => setSelectedDoctorId(e.target.value)}>
                  {doctorsList.map(doc => (
                    <MenuItem key={doc.id} value={doc.id}>
                      {doc.user?.name} ({doc.specialization})
                    </MenuItem>
                  ))}
                </Select>
              </FormControl>
              <TextField fullWidth label="Describe your Symptoms" multiline rows={3} margin="normal" value={symptoms} onChange={e => setSymptoms(e.target.value)} />
            </CardContent>
            <CardActions sx={{ padding: 2 }}>
              <Button variant="contained" color="secondary" onClick={handleBookConsultation}>Book Now</Button>
            </CardActions>
          </Card>
        </Grid>
        <Grid item xs={12} md={6}>
          <Card elevation={4} sx={{ mb: 3 }}>
            <CardContent>
              <Typography variant="h6" color="primary" gutterBottom>My Consultations</Typography>
              {myConsultations.length === 0 ? <Alert severity="info">No recent consultations.</Alert> : (
                myConsultations.map(c => (
                  <Box key={c.id} sx={{ mb: 2, p: 2, border: '1px solid #334155', borderRadius: 2 }}>
                    <Typography variant="subtitle2">{c.doctor?.user?.name} ({c.doctor?.specialization})</Typography>
                    <Typography variant="body2" color="text.secondary">Symptoms: {c.symptoms}</Typography>
                    <Chip label={c.status} size="small" color={c.status === 'SCHEDULED' ? 'primary' : c.status === 'IN_PROGRESS' ? 'warning' : 'success'} sx={{ mt: 1 }} />
                  </Box>
                ))
              )}
            </CardContent>
          </Card>

        </Grid>
      </Grid>
    );
  };

  const renderDoctorPortal = () => {
    const myPatients = consultations.filter(c => c.doctor?.id === userProfileId);
    
    return (
      <Grid container spacing={3}>
        <Grid item xs={12}>
          <Card elevation={4}>
            <CardContent>
              <Typography variant="h6" color="primary" gutterBottom>Active Consultations</Typography>
              {myPatients.length === 0 ? <Alert severity="info">No consultations scheduled.</Alert> : (
                <TableContainer>
                  <Table size="small">
                    <TableHead>
                      <TableRow>
                        <TableCell>Patient</TableCell>
                        <TableCell>Symptoms</TableCell>
                        <TableCell>Status</TableCell>
                        <TableCell>Action</TableCell>
                      </TableRow>
                    </TableHead>
                    <TableBody>
                      {myPatients.map(c => (
                        <TableRow key={c.id}>
                          <TableCell>{c.patient?.user?.name}</TableCell>
                          <TableCell>{c.symptoms}</TableCell>
                          <TableCell><Chip label={c.status} size="small" /></TableCell>
                          <TableCell>
                            {c.status === 'SCHEDULED' && (
                              <Button size="small" variant="contained" color="primary" onClick={() => handleStartConsultation(c.id)}>Start</Button>
                            )}
                            {c.status === 'IN_PROGRESS' && (
                              <Button size="small" variant="contained" color="secondary" onClick={() => initiateCompleteConsultation(c)}>Diagnose & Prescribe</Button>
                            )}
                            {c.status === 'COMPLETED' && (
                              <Typography variant="caption" color="text.secondary">Done</Typography>
                            )}
                          </TableCell>
                        </TableRow>
                      ))}
                    </TableBody>
                  </Table>
                </TableContainer>
              )}
            </CardContent>
          </Card>
        </Grid>
      </Grid>
    );
  };

  const renderPharmacistPortal = () => {
    return (
      <Grid container spacing={3}>
        <Grid item xs={12}>
          <Card elevation={4}>
            <CardContent>
              <Typography variant="h6" color="primary" gutterBottom>Prescription Verification Queue</Typography>
              {prescriptions.length === 0 ? <Alert severity="warning">No prescriptions actively pending.</Alert> : (
                <TableContainer>
                  <Table size="small">
                    <TableHead>
                      <TableRow>
                        <TableCell>Patient</TableCell>
                        <TableCell>Medicine (Items)</TableCell>
                        <TableCell>Status</TableCell>
                        <TableCell>Action</TableCell>
                      </TableRow>
                    </TableHead>
                    <TableBody>
                      {prescriptions.map(p => (
                        <TableRow key={p.id}>
                          <TableCell>{p.patient?.user?.name}</TableCell>
                          <TableCell>{p.items?.reduce((acc, item) => acc + item.medicineName + " ", "")}</TableCell>
                          <TableCell><Chip label={p.status} size="small" color={p.status==='ACTIVE' ? 'primary' : 'success'} /></TableCell>
                          <TableCell>
                            {p.status === 'ACTIVE' && (
                              <Button size="small" variant="outlined" color="secondary" onClick={() => handleDispensePrescription(p.id)}>Verify & Dispense</Button>
                            )}
                          </TableCell>
                        </TableRow>
                      ))}
                    </TableBody>
                  </Table>
                </TableContainer>
              )}
            </CardContent>
          </Card>
        </Grid>
      </Grid>
    );
  };

  const renderAdminPortal = () => (
    <Grid container spacing={3}>
      <Grid item xs={12}>
        <Card elevation={4}>
          <CardContent>
            <Typography variant="h6" color="primary" gutterBottom>System Authorization Audit</Typography>
            <Typography variant="body2" color="text.secondary" mb={2}>Monitor internal roles and system compliance.</Typography>
            <TableContainer sx={{ mt: 2 }}>
              <Table size="small">
                <TableHead>
                  <TableRow>
                    <TableCell><b>ID</b></TableCell>
                    <TableCell><b>Name</b></TableCell>
                    <TableCell><b>Role</b></TableCell>
                    <TableCell><b>Status</b></TableCell>
                  </TableRow>
                </TableHead>
                <TableBody>
                  {users.map((row) => (
                    <TableRow key={row.id}>
                      <TableCell>{row.id}</TableCell>
                      <TableCell>{row.name}</TableCell>
                      <TableCell><Chip label={row.role} size="small" /></TableCell>
                      <TableCell><Chip label="Active" size="small" color="success" variant="outlined" /></TableCell>
                    </TableRow>
                  ))}
                </TableBody>
              </Table>
            </TableContainer>
          </CardContent>
        </Card>
      </Grid>
      <Grid item xs={12}>
        <Card elevation={4}>
           <CardContent>
              <Typography variant="h6" color="primary" gutterBottom>Appointment Monitor</Typography>
              <TableContainer>
                <Table size="small">
                  <TableHead><TableRow><TableCell>Patient</TableCell><TableCell>Doctor</TableCell><TableCell>Status</TableCell></TableRow></TableHead>
                  <TableBody>
                    {consultations.map(c => (
                      <TableRow key={c.id}>
                        <TableCell>{c.patient?.user?.name}</TableCell>
                        <TableCell>{c.doctor?.user?.name}</TableCell>
                        <TableCell><Chip label={c.status} size="small" /></TableCell>
                      </TableRow>
                    ))}
                  </TableBody>
                </Table>
              </TableContainer>
           </CardContent>
        </Card>
      </Grid>
    </Grid>
  );

  return (
    <ThemeProvider theme={theme}>
      <CssBaseline />
      <AppBar position="static" elevation={0} sx={{ background: '#1e293b', borderBottom: '1px solid #334155' }}>
        <Toolbar>
          <LocalHospitalIcon color="primary" sx={{ mr: 2 }} />
          <Typography variant="h6" component="div" sx={{ flexGrow: 1, fontWeight: 'bold' }}>
            NexaHealth System
          </Typography>
          <Typography variant="body2" sx={{ mr: 2 }}>
            Logged in as <b>{currentUser?.name}</b>
          </Typography>
          <Chip label={currentUser?.role} color="primary" size="small" sx={{ mr: 3 }} />
          <IconButton color="inherit" onClick={logout}>
            <LogoutIcon />
          </IconButton>
        </Toolbar>
        <Tabs value={tabIndex} onChange={(e, val) => setTabIndex(val)} textColor="inherit" indicatorColor="primary" sx={{ px: 2 }}>
          <Tab label="My Portal" />
          <Tab label="Network Directory" />
        </Tabs>
      </AppBar>

      <Container maxWidth="lg" sx={{ mt: 4, mb: 4 }}>
        {tabIndex === 0 && (
          <Box className="fade-in">
            <Typography variant="h4" fontWeight="bold" gutterBottom>
              {currentUser.role === 'PATIENT' && 'Patient Services'}
              {currentUser.role === 'DOCTOR' && 'Doctor Workspace'}
              {currentUser.role === 'PHARMACIST' && 'Pharmacy Control'}
              {currentUser.role === 'ADMIN' && 'System Administration'}
            </Typography>
            {currentUser.role === 'PATIENT' && renderPatientPortal()}
            {currentUser.role === 'DOCTOR' && renderDoctorPortal()}
            {currentUser.role === 'PHARMACIST' && renderPharmacistPortal()}
            {currentUser.role === 'ADMIN' && renderAdminPortal()}
          </Box>
        )}

        {tabIndex === 1 && (
          <Box className="fade-in">
            <Box display="flex" justifyContent="space-between" alignItems="center" mb={3}>
              <Typography variant="h5" fontWeight="bold">Global Network Directory</Typography>
              <Button startIcon={<RefreshIcon />} variant="outlined" onClick={fetchGlobalData}>
                Refresh Data
              </Button>
            </Box>
            <TableContainer component={Paper} elevation={8}>
              <Table sx={{ minWidth: 650 }}>
                <TableHead sx={{ backgroundColor: '#0f172a' }}>
                  <TableRow>
                    <TableCell><b>ID</b></TableCell>
                    <TableCell><b>Name</b></TableCell>
                    <TableCell><b>Email</b></TableCell>
                    <TableCell><b>Role</b></TableCell>
                  </TableRow>
                </TableHead>
                <TableBody>
                  {users.map((row) => (
                    <TableRow key={row.id} hover>
                      <TableCell>{row.id}</TableCell>
                      <TableCell>{row.name}</TableCell>
                      <TableCell>{row.email}</TableCell>
                      <TableCell>
                        <Chip label={row.role} size="small" color={row.role === 'DOCTOR' ? 'success' : row.role === 'PATIENT' ? 'primary' : row.role === 'ADMIN' ? 'error' : 'warning'} />
                      </TableCell>
                    </TableRow>
                  ))}
                  {users.length === 0 && (
                    <TableRow><TableCell colSpan={4} align="center">No users found. Wait for data to load.</TableCell></TableRow>
                  )}
                </TableBody>
              </Table>
            </TableContainer>
          </Box>
        )}
      </Container>
      
      {/* Doctor Prescription Creation Modal */}
      <Dialog open={isPrescriptionModalOpen} onClose={() => setPrescriptionModalOpen(false)} maxWidth="sm" fullWidth PaperProps={{ style: { background: '#1e293b' } }}>
        <DialogTitle sx={{ color: '#6366f1', fontWeight: 'bold' }}>Complete Consultation & Prescribe</DialogTitle>
        <DialogContent dividers>
          <Typography variant="subtitle2" mb={2}>Patient: {activeConsultation?.patient?.user?.name}</Typography>
          
          <TextField fullWidth label="Diagnosis" margin="dense" value={diag} onChange={e => setDiag(e.target.value)} />
          <TextField fullWidth label="Consultation Notes" margin="dense" multiline rows={2} value={notes} onChange={e => setNotes(e.target.value)} />
          
          <Typography variant="subtitle2" mt={3} mb={1} color="secondary">E-Prescription Details</Typography>
          <TextField fullWidth label="Medicine Name" margin="dense" value={medName} onChange={e => setMedName(e.target.value)} />
          <Box display="flex" gap={2}>
            <TextField fullWidth label="Dosage (e.g. 500mg)" margin="dense" value={dosage} onChange={e => setDosage(e.target.value)} />
            <TextField fullWidth label="Frequency (e.g. 1x Day)" margin="dense" value={freq} onChange={e => setFreq(e.target.value)} />
          </Box>
          <TextField fullWidth label="Duration (e.g. 7 days)" margin="dense" value={duration} onChange={e => setDuration(e.target.value)} />
        </DialogContent>
        <DialogActions>
          <Button onClick={() => setPrescriptionModalOpen(false)}>Cancel</Button>
          <Button variant="contained" color="secondary" onClick={submitPrescriptionAndComplete}>Issue Prescription & Complete</Button>
        </DialogActions>
      </Dialog>
    </ThemeProvider>
  );
}
